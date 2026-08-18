#!/usr/bin/env bash
set -euo pipefail

release_id="${1:?release id is required}"
root="/project/ruoyi"
staging="/home/ubuntu/yuncheng-bluegreen-${release_id}"
state_file="${root}/bluegreen-state"

if [[ ! -f "${staging}/ruoyi-admin.jar" || ! -f "${staging}/pc.tgz" ]]; then
    echo "Release staging files are missing" >&2
    exit 1
fi

# shellcheck disable=SC1090
source "${state_file}"
if [[ "${active_slot}" != "a" && "${active_slot}" != "b" ]]; then
    echo "The active slot must be a or b" >&2
    exit 1
fi

if [[ "${active_slot}" == "a" ]]; then
    next_slot=b
    next_port=8082
else
    next_slot=a
    next_port=8081
fi

sudo install -d -o ubuntu -g ubuntu -m 0755 "${root}/backend-${next_slot}"
sudo install -o ubuntu -g ubuntu -m 0644 "${staging}/ruoyi-admin.jar" \
    "${root}/backend-${next_slot}/ruoyi-admin.jar"
sudo install -d -o ubuntu -g ubuntu -m 0755 "${root}/releases/${release_id}/pc"
sudo tar -xzf "${staging}/pc.tgz" -C "${root}/releases/${release_id}/pc"
sudo chown -R www-data:www-data "${root}/releases/${release_id}/pc"

# Copy active production secrets, then keep the new slot in Quartz standby.
sudo cp "${root}/env/${active_slot}" "${root}/env/${next_slot}"
sudo sed -i \
    -e "s#^SERVER_PORT=.*#SERVER_PORT=${next_port}#" \
    -e "s#^JAR_PATH=.*#JAR_PATH=${root}/backend-${next_slot}/ruoyi-admin.jar#" \
    -e "s#^YUNCHENG_RELEASE_ID=.*#YUNCHENG_RELEASE_ID=${release_id}#" \
    -e 's#^RUOYI_QUARTZ_ENABLED=.*#RUOYI_QUARTZ_ENABLED=false#' \
    -e 's#^SPRING_QUARTZ_AUTO_STARTUP=.*#SPRING_QUARTZ_AUTO_STARTUP=false#' \
    "${root}/env/${next_slot}"
token="$(openssl rand -hex 32)"
if grep -q '^YUNCHENG_QUARTZ_ACTIVATION_TOKEN=' "${root}/env/${next_slot}"; then
    sudo sed -i "s#^YUNCHENG_QUARTZ_ACTIVATION_TOKEN=.*#YUNCHENG_QUARTZ_ACTIVATION_TOKEN=${token}#" "${root}/env/${next_slot}"
else
    printf 'YUNCHENG_QUARTZ_ACTIVATION_TOKEN=%s\n' "${token}" | sudo tee -a "${root}/env/${next_slot}" >/dev/null
fi
sudo chown root:ubuntu "${root}/env/${next_slot}"
sudo chmod 0640 "${root}/env/${next_slot}"

sudo systemctl daemon-reload
sudo systemctl restart "ruoyi-admin@${next_slot}"

ready=0
for attempt in 1 2 3 4 5 6 7 8 9 10 11 12; do
    if [[ "$(curl -sS -o /dev/null -w '%{http_code}' "http://127.0.0.1:${next_port}/internal/health/ready" || true)" == "200" ]]; then
        ready=1
        break
    fi
    sleep 5
done
if [[ "${ready}" != 1 ]]; then
    sudo journalctl -u "ruoyi-admin@${next_slot}" -n 120 --no-pager
    exit 1
fi

sudo sed -i -E "s#127\.0\.0\.1:[0-9]+#127.0.0.1:${next_port}#" /etc/nginx/conf.d/ruoyi-upstream.conf
sudo nginx -t
sudo systemctl reload nginx
sleep 2

sudo systemctl stop "ruoyi-admin@${active_slot}"
token="$(sudo sed -n 's/^YUNCHENG_QUARTZ_ACTIVATION_TOKEN=//p' "${root}/env/${next_slot}")"
curl -fsS -X POST -H "X-Yuncheng-Activation-Token: ${token}" \
    "http://127.0.0.1:${next_port}/internal/health/quartz/activate" >/dev/null
sudo sed -i \
    -e 's#^RUOYI_QUARTZ_ENABLED=.*#RUOYI_QUARTZ_ENABLED=true#' \
    -e 's#^SPRING_QUARTZ_AUTO_STARTUP=.*#SPRING_QUARTZ_AUTO_STARTUP=true#' \
    "${root}/env/${next_slot}"

sudo ln -s "${root}/releases/${release_id}/pc" "${root}/www/pc-current.next"
sudo mv -Tf "${root}/www/pc-current.next" "${root}/www/pc-current"
sudo systemctl reload nginx

printf 'release=%s\nactive_port=%s\nactive_slot=%s\nprevious_release=%s\nprevious_slot=%s\n' \
    "${release_id}" "${next_port}" "${next_slot}" "${release}" "${active_slot}" \
    | sudo tee "${state_file}" >/dev/null
sudo chown root:ubuntu "${state_file}"
sudo chmod 0640 "${state_file}"
echo "Promoted ${release_id} on slot ${next_slot}."

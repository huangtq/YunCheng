#!/usr/bin/env bash
set -euo pipefail

root="/project/ruoyi"
state_file="${root}/bluegreen-state"
# shellcheck disable=SC1090
source "${state_file}"

if [[ "${previous_slot}" != "a" && "${previous_slot}" != "b" ]]; then
    echo "No rollback slot is recorded" >&2
    exit 1
fi
previous_port="$(sed -n 's/^SERVER_PORT=//p' "${root}/env/${previous_slot}")"
sudo sed -i \
    -e 's#^RUOYI_QUARTZ_ENABLED=.*#RUOYI_QUARTZ_ENABLED=false#' \
    -e 's#^SPRING_QUARTZ_AUTO_STARTUP=.*#SPRING_QUARTZ_AUTO_STARTUP=false#' \
    "${root}/env/${previous_slot}"
sudo systemctl start "ruoyi-admin@${previous_slot}"
ready=0
for attempt in 1 2 3 4 5 6 7 8 9 10 11 12; do
    if [[ "$(curl -sS -o /dev/null -w '%{http_code}' "http://127.0.0.1:${previous_port}/internal/health/ready" || true)" == "200" ]]; then
        ready=1
        break
    fi
    sleep 5
done
if [[ "${ready}" != 1 ]]; then
    sudo journalctl -u "ruoyi-admin@${previous_slot}" -n 120 --no-pager
    exit 1
fi

sudo sed -i -E "s#127\.0\.0\.1:[0-9]+#127.0.0.1:${previous_port}#" /etc/nginx/conf.d/ruoyi-upstream.conf
sudo nginx -t
sudo systemctl reload nginx
sudo systemctl stop "ruoyi-admin@${active_slot}"
token="$(sudo sed -n 's/^YUNCHENG_QUARTZ_ACTIVATION_TOKEN=//p' "${root}/env/${previous_slot}")"
curl -fsS -X POST -H "X-Yuncheng-Activation-Token: ${token}" \
    "http://127.0.0.1:${previous_port}/internal/health/quartz/activate" >/dev/null
sudo sed -i \
    -e 's#^RUOYI_QUARTZ_ENABLED=.*#RUOYI_QUARTZ_ENABLED=true#' \
    -e 's#^SPRING_QUARTZ_AUTO_STARTUP=.*#SPRING_QUARTZ_AUTO_STARTUP=true#' \
    "${root}/env/${previous_slot}"

if [[ "${previous_release}" == "legacy" ]]; then
    static_path="${root}/www/pc"
else
    static_path="${root}/releases/${previous_release}/pc"
fi
sudo ln -s "${static_path}" "${root}/www/pc-current.rollback"
sudo mv -Tf "${root}/www/pc-current.rollback" "${root}/www/pc-current"
sudo systemctl reload nginx

printf 'release=%s\nactive_port=%s\nactive_slot=%s\nprevious_release=%s\nprevious_slot=%s\n' \
    "${previous_release}" "${previous_port}" "${previous_slot}" "${release}" "${active_slot}" \
    | sudo tee "${state_file}" >/dev/null
sudo chown root:ubuntu "${state_file}"
sudo chmod 0640 "${state_file}"
echo "Rolled back to ${previous_release} on slot ${previous_slot}."

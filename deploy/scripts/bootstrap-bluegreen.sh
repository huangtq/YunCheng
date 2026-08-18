#!/usr/bin/env bash
set -euo pipefail

release_id="${1:?release id is required}"
staging="/home/ubuntu/yuncheng-bluegreen-${release_id}"
root="/project/ruoyi"
current_pid="$(pgrep -f 'java.*ruoyi-admin.jar' | head -n 1 || true)"

if [[ -z "${current_pid}" ]]; then
    echo "Cannot find the current ruoyi-admin process" >&2
    exit 1
fi

sudo install -d -o ubuntu -g ubuntu -m 0755 \
    "${root}/backend-a" "${root}/backend-b" "${root}/env" \
    "${root}/releases/${release_id}/pc"
sudo install -o ubuntu -g ubuntu -m 0644 "${staging}/ruoyi-admin.jar" "${root}/backend-a/ruoyi-admin.jar"
sudo install -o ubuntu -g ubuntu -m 0644 "${staging}/ruoyi-admin.jar" "${root}/backend-b/ruoyi-admin.jar"
sudo tar -xzf "${staging}/pc.tgz" -C "${root}/releases/${release_id}/pc"
sudo chown -R www-data:www-data "${root}/releases/${release_id}/pc"

activation_token="$(openssl rand -hex 32)"
env_dump="$(tr '\0' '\n' < "/proc/${current_pid}/environ")"

write_env() {
    local slot="$1"
    local port="$2"
    local target="${root}/env/${slot}"
    {
        printf 'SERVER_PORT=%s\n' "${port}"
        printf 'JAR_PATH=%s\n' "${root}/backend-${slot}/ruoyi-admin.jar"
        printf 'YUNCHENG_RELEASE_ID=%s\n' "${release_id}"
        printf 'YUNCHENG_QUARTZ_ACTIVATION_TOKEN=%s\n' "${activation_token}"
        printf 'RUOYI_QUARTZ_ENABLED=false\n'
        printf 'SPRING_QUARTZ_AUTO_STARTUP=false\n'
        for key in RUOYI_DB_PASSWORD RUOYI_DB_USERNAME RUOYI_REDIS_PASSWORD \
            WECHAT_MP_APP_ID WECHAT_MP_SECRET WECHAT_MP_OAUTH_REDIRECT \
            WECHAT_MP_H5_HOME WECHAT_MP_PUBLIC_BASE WECHAT_MP_SHARE_PAGE \
            WECHAT_MP_MOCK_ENABLED YUNCHENG_PROFILE_PATH; do
            value="$(printf '%s\n' "${env_dump}" | sed -n "s/^${key}=//p" | head -n 1)"
            if [[ -n "${value}" ]]; then
                printf '%s=%s\n' "${key}" "${value}"
            fi
        done
    } | sudo tee "${target}.tmp" >/dev/null
    sudo chown root:ubuntu "${target}.tmp"
    sudo chmod 0640 "${target}.tmp"
    sudo mv -f "${target}.tmp" "${target}"
}

write_env a 8081
write_env b 8082

sudo install -o root -g root -m 0644 "${staging}/ruoyi-admin@.service" /etc/systemd/system/ruoyi-admin@.service
sed 's/127\.0\.0\.1:8081/127.0.0.1:8080/' "${staging}/ruoyi-upstream.conf" \
    | sudo tee /etc/nginx/conf.d/ruoyi-upstream.conf >/dev/null

if [[ ! -e "${root}/www/pc-current" ]]; then
    sudo ln -s "${root}/www/pc" "${root}/www/pc-current"
fi
if [[ ! -e "${root}/www/h5-current" ]]; then
    sudo ln -s "${root}/www/h5" "${root}/www/h5-current"
fi

sudo cp /etc/nginx/sites-enabled/ruoyi "${root}/releases/${release_id}/ruoyi.nginx.before"
sudo sed -i \
    -e 's#/project/ruoyi/www/pc;#/project/ruoyi/www/pc-current;#g' \
    -e 's#http://127\.0\.0\.1:8080#http://ruoyi_backend#g' \
    /etc/nginx/sites-enabled/ruoyi
sudo nginx -t
sudo systemctl reload nginx
sudo systemctl daemon-reload
printf 'release=%s\nactive_port=8080\nactive_slot=legacy\n' "${release_id}" \
    | sudo tee "${root}/bluegreen-state" >/dev/null
sudo chown root:ubuntu "${root}/bluegreen-state"
sudo chmod 0640 "${root}/bluegreen-state"

echo "Blue-green base installed for ${release_id}. The legacy 8080 service is still active."

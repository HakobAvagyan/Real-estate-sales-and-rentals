get_keys() {
    grep '=' "$1" | grep -v '^#' | cut -d'=' -f1 | sed 's/^[[:space:]]*//;s/[[:space:]]*$//' | sort | uniq
}

# Keys from files
DEFAULT_KEYS=$(get_keys ../messages.properties)
RU_KEYS=$(get_keys ../messages_ru.properties)
HY_KEYS=$(get_keys ../messages_hy.properties)

# Keys used in templates (already generated in previous step, but let's re-extract to be sure)
USED_KEYS=$(grep -rnoP "#\{[a-zA-Z0-9._-]+\}" . | sed -n 's/.*#{\(.*\)}/\1/p' | sort | uniq)

echo "--- KEYS USED IN TEMPLATES BUT MISSING IN DEFAULT messages.properties ---"
for k in $USED_KEYS; do
    if ! echo "$DEFAULT_KEYS" | grep -q "^$k$"; then
        echo "$k"
    fi
done

echo ""
echo "--- KEYS IN DEFAULT BUT MISSING IN RU ---"
for k in $DEFAULT_KEYS; do
    if ! echo "$RU_KEYS" | grep -q "^$k$"; then
        echo "$k"
    fi
done

echo ""
echo "--- KEYS IN DEFAULT BUT MISSING IN HY ---"
for k in $DEFAULT_KEYS; do
    if ! echo "$HY_KEYS" | grep -q "^$k$"; then
        echo "$k"
    fi
done

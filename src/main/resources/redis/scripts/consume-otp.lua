local current = redis.call('GET', KEYS[1])
if not current then
    return -1
end
if current ~= ARGV[1] then
    local failures = redis.call('INCR', KEYS[2])
    local ttl = redis.call('TTL', KEYS[1])
    if ttl > 0 then
        redis.call('EXPIRE', KEYS[2], ttl)
    end
    if failures >= tonumber(ARGV[2]) then
        redis.call('DEL', KEYS[1])
        redis.call('DEL', KEYS[2])
    end
    return 0
end
redis.call('DEL', KEYS[1])
redis.call('DEL', KEYS[2])
return 1

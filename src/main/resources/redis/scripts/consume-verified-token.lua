local current = redis.call('GET', KEYS[1])
if not current then
    return nil
end
if ARGV[1] ~= '' and current ~= ARGV[1] then
    return nil
end
redis.call('DEL', KEYS[1])
return current

local requests = redis.call('INCR', KEYS[1])
if requests == 1 then
    redis.call('EXPIRE', KEYS[1], tonumber(ARGV[1]))
end
if requests > tonumber(ARGV[2]) then
    return 0
end
return 1

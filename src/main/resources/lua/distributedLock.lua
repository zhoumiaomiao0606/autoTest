--
-- Created by IntelliJ IDEA.
-- User: junzijian
-- Date: 2018/8/3
-- Time: 18:00
-- To change this template use File | Settings | File Templates.
-- rateLimit

local key = KEYS[1]
local value = ARGV[1]
local pExpire = ARGV[2]

if redis.call("SET", key, value, "NX", "PX", pExpire) then
    return 1
elseif redis.call("TTL", key) == -1 then
    redis.call("PEXPIRE", key, pExpire)
end
return 0



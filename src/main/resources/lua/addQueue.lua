--
-- Created by IntelliJ IDEA.
-- User: liuzhe
-- Date: 2018/9/27
-- Time: 下午4:59
-- To change this template use File | Settings | File Templates.
--

local key = KEYS[1]
local expire = ARGV[1]
local startTime = tonumber(ARGV[2])

if redis.call("SETEX", key, expire, startTime) then
    return 1
elseif redis.call("TTL", key) == -1 then
    redis.call("EXPIRE", key, expire)
end
return 0
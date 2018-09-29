--
-- Created by IntelliJ IDEA.
-- User: liuzhe
-- Date: 2018/9/27
-- Time: 下午4:59
-- To change this template use File | Settings | File Templates.
--

local key = KEYS[1]
local startTime = ARGV[1]
local expire = ARGV[2]

if redis.call("SET", key, startTime, "NX", "EX", expire) then
    return 1
elseif redis.call("EXPIRE", key, expire) then
    return 1
end
return 0
--
-- Created by IntelliJ IDEA.
-- User: liuzhe
-- Date: 2018/8/3
-- Time: 下午6:43
-- To change this template use File | Settings | File Templates.
--

local key = KEYS[1]
local value = ARGV[1]


local val_ = redis.call("GET", key);

if val_ == nil then
    return 1
elseif val_ == value then
    if redis.call("DEL", key) then
        return 1
    end
end
return 0

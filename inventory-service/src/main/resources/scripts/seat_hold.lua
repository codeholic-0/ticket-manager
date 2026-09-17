-- seat_hold.lua
--
-- KEYS[1..n]   : hold keys, one per seat: "hold:{eventId}:{seatId}"
-- ARGV[1]      : hold token (unique per reservation)
-- ARGV[2]      : hold TTL in seconds
--
-- Return (unambiguous integer):
--    1   -> success, all seats held (or already held by our token, TTL refreshed)
--   -i   -> failure, seat #i (1-based) is blocked by another token; NO keys written
--   -1   -> empty seat set (guard)

local n = #KEYS
if n == 0 then
    return -1
end

-- Phase 1: verify every seat is free OR already held by OUR token
for i = 1, n do
    local existing = redis.call('GET', KEYS[i])
    if existing then
        if existing ~= ARGV[1] then
            return -i          -- held by someone else, abort with zero writes
        end
    end
end

-- Phase 2: write all holds (all-or-nothing: Phase 1 passed for every seat)
for i = 1, n do
    redis.call('SET', KEYS[i], ARGV[1], 'EX', ARGV[2])
end

return 1
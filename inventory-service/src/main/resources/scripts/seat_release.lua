-- seat_release.lua
--
-- KEYS[1..n]   : hold keys, one per seat: "hold:{eventId}:{seatId}"
-- ARGV[1]      : hold token expected to own the holds
--
-- Return: number of keys actually deleted (0..n).
-- Token-scoped: only deletes a hold whose value equals our token, so a stale
-- reservation can never release a newer holder's hold. Already-expired (nil)
-- keys are skipped.

local n = #KEYS
local released = 0

for i = 1, n do
    local existing = redis.call('GET', KEYS[i])
    if existing == ARGV[1] then
        redis.call('DEL', KEYS[i])
        released = released + 1
    end
end

return released
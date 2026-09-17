package dev.zen.inventory.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.redisson.client.codec.StringCodec;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import dev.zen.inventory.domain.HoldResult;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatHoldService {

    private static final String HOLD_KEY_PREFIX = "hold:";
    private static final long HOLD_TTL_SECONDS = 600L;

    private final RedissonClient redissonClient;

    private RScript script;
    private String holdSha;
    private String releaseSha;

    private List<Object> holdKeys(UUID eventId, List<UUID> seatIds) {
        return seatIds.stream()
                .map(seatId -> (Object) (HOLD_KEY_PREFIX + eventId + ":" + seatId))
                .toList();
    }

    private String readScript(String path) {
        try (var in = new ClassPathResource(path).getInputStream()) {
            return StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load Lua script: " + path, e);
        }
    }

    private long eval(String sha, List<Object> keys, Object... values) {
        try {
            return script.<Long>evalSha(
                    RScript.Mode.READ_WRITE, sha, RScript.ReturnType.LONG, keys, values);
        } catch (RedisException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("NOSCRIPT")) {
                String reloaded = reload(sha);
                return script.<Long>evalSha(
                        RScript.Mode.READ_WRITE, reloaded, RScript.ReturnType.LONG, keys, values);
            }
            throw ex;
        }
    }

    private String reload(String staleSha) {
        if (staleSha.equals(holdSha)) {
            return holdSha = script.scriptLoad(readScript("scripts/seat_hold.lua"));
        }
        return releaseSha = script.scriptLoad(readScript("scripts/seat_release.lua"));
    }

    private HoldResult toHoldResult(long code, List<UUID> seatIds) {
        if (code == 1L) {
            return new HoldResult.Success(seatIds);
        }
        if (code == -1L) {
            return new HoldResult.BadRequest("empty seat set");
        }
        int index = (int) -code - 1;
        if (index < 0 || index >= seatIds.size()) {
            return new HoldResult.BadRequest("unexpected script result: " + code);
        }
        return new HoldResult.Blocked(seatIds.get(index));
    }

    @PostConstruct
    void loadScripts() {
        script = redissonClient.getScript(StringCodec.INSTANCE);
        holdSha = script.scriptLoad(readScript("scripts/seat_hold.lua"));
        releaseSha = script.scriptLoad(readScript("scripts/seat_release.lua"));
    }

    public HoldResult hold(UUID eventId, List<UUID> seatIds, String token) {
        if (seatIds == null || seatIds.isEmpty()) {
            return new HoldResult.BadRequest("seat set is empty");
        }
        long code = eval(holdSha, holdKeys(eventId, seatIds), token, HOLD_TTL_SECONDS);
        return toHoldResult(code, seatIds);
    }

    public long release(UUID eventId, List<UUID> seatIds, String token) {
        if (seatIds == null || seatIds.isEmpty()) {
            return 0L;
        }
        return eval(releaseSha, holdKeys(eventId, seatIds), token);
    }

    public long releaseExpired() {
        // v1: expiry is handled by the per-key TTL. DB reconciliation will be added
        // later
        return 0L;
    }
}
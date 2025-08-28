package com.honeymorning.api.common.security.core;

public record Token(String header, int expirySeconds) {
}
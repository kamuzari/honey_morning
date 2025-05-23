package com.sf.honeymorning.common.security.core;

public record Token(String header, int expirySeconds) {
}
package com.sf.honeymorning.user.authentication.model;

public record Token(String header, int expirySeconds) {
}
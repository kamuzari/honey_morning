package com.sf.honeymorning.user.adapter.in.authentication.model;

public record Token(String header, int expirySeconds) {
}
package com.sf.honeymorning.account.authenticater.model;

public record Token(String header, int expirySeconds) {
}
package com.seva.seva_mobile_version.dto.request;

import jakarta.validation.constraints.Email;

public record LoginRequestOwner(@Email  String email, String password) {
}

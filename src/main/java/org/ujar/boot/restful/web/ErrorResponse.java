package org.ujar.boot.restful.web;

import java.util.List;

public record ErrorResponse<M>(List<Error<M>> errors) {
}

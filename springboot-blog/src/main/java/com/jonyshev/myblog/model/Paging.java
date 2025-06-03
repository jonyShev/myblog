package com.jonyshev.myblog.model;

public record Paging(int pageNumber, int pageSize, boolean hasNext, boolean hasPrevious) {}
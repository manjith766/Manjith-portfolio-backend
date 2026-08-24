package com.manjith.portfolio.entity;

/**
 * Mirrors the CHECK constraint on categories.type in V1__initial_schema.sql:
 * CONSTRAINT chk_categories_type CHECK (type IN ('SKILL', 'PROJECT', 'BLOG'))
 */
public enum CategoryType {
    SKILL,
    PROJECT,
    BLOG
}

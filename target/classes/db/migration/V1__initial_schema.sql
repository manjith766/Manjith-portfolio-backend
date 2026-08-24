-- =====================================================================
-- V1__initial_schema.sql
-- Portfolio Platform — Initial Schema
-- Target: PostgreSQL 15+
-- =====================================================================

-- ---------------------------------------------------------------------
-- EXTENSIONS
-- ---------------------------------------------------------------------
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ---------------------------------------------------------------------
-- 1. ROLES
-- ---------------------------------------------------------------------
CREATE TABLE roles (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_roles_name UNIQUE (name)
);

-- ---------------------------------------------------------------------
-- 2. USERS
-- ---------------------------------------------------------------------
CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    role_id         BIGINT NOT NULL,
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE RESTRICT
);
CREATE INDEX idx_users_role_id ON users (role_id);

-- ---------------------------------------------------------------------
-- 3. REFRESH TOKENS
-- ---------------------------------------------------------------------
CREATE TABLE refresh_tokens (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    token           VARCHAR(512) NOT NULL,
    expiry_date     TIMESTAMPTZ NOT NULL,
    revoked         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_refresh_tokens_token UNIQUE (token),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);

-- ---------------------------------------------------------------------
-- 4. CATEGORIES  (shared taxonomy: SKILL | PROJECT | BLOG)
-- ---------------------------------------------------------------------
CREATE TABLE categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    slug        VARCHAR(120) NOT NULL,
    type        VARCHAR(20) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_categories_slug_type UNIQUE (slug, type),
    CONSTRAINT chk_categories_type CHECK (type IN ('SKILL', 'PROJECT', 'BLOG'))
);
CREATE INDEX idx_categories_type ON categories (type);

-- ---------------------------------------------------------------------
-- 5. SKILLS
-- ---------------------------------------------------------------------
CREATE TABLE skills (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    category_id         BIGINT NOT NULL,
    proficiency_pct     SMALLINT NOT NULL,
    years_experience    NUMERIC(4,1) NOT NULL DEFAULT 0,
    icon_url            VARCHAR(500),
    display_order       INTEGER NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_skills_name UNIQUE (name),
    CONSTRAINT fk_skills_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT,
    CONSTRAINT chk_skills_proficiency CHECK (proficiency_pct BETWEEN 0 AND 100),
    CONSTRAINT chk_skills_years CHECK (years_experience >= 0)
);
CREATE INDEX idx_skills_category_id ON skills (category_id);

-- ---------------------------------------------------------------------
-- 6. PROJECTS
-- ---------------------------------------------------------------------
CREATE TABLE projects (
    id                  BIGSERIAL PRIMARY KEY,
    title               VARCHAR(200) NOT NULL,
    slug                VARCHAR(220) NOT NULL,
    description         TEXT NOT NULL,
    short_description   VARCHAR(500),
    category_id         BIGINT,
    github_url          VARCHAR(500),
    live_demo_url       VARCHAR(500),
    cover_image_url     VARCHAR(500),
    is_featured         BOOLEAN NOT NULL DEFAULT FALSE,
    display_order       INTEGER NOT NULL DEFAULT 0,
    view_count          BIGINT NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_projects_slug UNIQUE (slug),
    CONSTRAINT fk_projects_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL,
    CONSTRAINT chk_projects_view_count CHECK (view_count >= 0)
);
CREATE INDEX idx_projects_category_id ON projects (category_id);
CREATE INDEX idx_projects_is_featured ON projects (is_featured);

-- ---------------------------------------------------------------------
-- 7. PROJECT_SKILLS (tech stack, many-to-many)
-- ---------------------------------------------------------------------
CREATE TABLE project_skills (
    project_id  BIGINT NOT NULL,
    skill_id    BIGINT NOT NULL,
    PRIMARY KEY (project_id, skill_id),
    CONSTRAINT fk_project_skills_project FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE,
    CONSTRAINT fk_project_skills_skill FOREIGN KEY (skill_id) REFERENCES skills (id) ON DELETE CASCADE
);
CREATE INDEX idx_project_skills_skill_id ON project_skills (skill_id);

-- ---------------------------------------------------------------------
-- 8. PROJECT_IMAGES
-- ---------------------------------------------------------------------
CREATE TABLE project_images (
    id              BIGSERIAL PRIMARY KEY,
    project_id      BIGINT NOT NULL,
    image_url       VARCHAR(500) NOT NULL,
    alt_text        VARCHAR(255),
    display_order   INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_project_images_project FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE
);
CREATE INDEX idx_project_images_project_id ON project_images (project_id);

-- ---------------------------------------------------------------------
-- 9. PROJECT_FEATURES
-- ---------------------------------------------------------------------
CREATE TABLE project_features (
    id              BIGSERIAL PRIMARY KEY,
    project_id      BIGINT NOT NULL,
    feature_text    VARCHAR(500) NOT NULL,
    display_order   INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_project_features_project FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE
);
CREATE INDEX idx_project_features_project_id ON project_features (project_id);

-- ---------------------------------------------------------------------
-- 10. EXPERIENCE
-- ---------------------------------------------------------------------
CREATE TABLE experience (
    id              BIGSERIAL PRIMARY KEY,
    company_name    VARCHAR(200) NOT NULL,
    role            VARCHAR(200) NOT NULL,
    location        VARCHAR(200),
    start_date      DATE NOT NULL,
    end_date        DATE,
    is_current      BOOLEAN NOT NULL DEFAULT FALSE,
    description     TEXT,
    display_order   INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_experience_dates CHECK (end_date IS NULL OR end_date >= start_date)
);

-- ---------------------------------------------------------------------
-- 11. EXPERIENCE_RESPONSIBILITIES
-- ---------------------------------------------------------------------
CREATE TABLE experience_responsibilities (
    id              BIGSERIAL PRIMARY KEY,
    experience_id   BIGINT NOT NULL,
    text            VARCHAR(500) NOT NULL,
    display_order   INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_experience_resp_experience FOREIGN KEY (experience_id) REFERENCES experience (id) ON DELETE CASCADE
);
CREATE INDEX idx_experience_resp_experience_id ON experience_responsibilities (experience_id);

-- ---------------------------------------------------------------------
-- 12. EXPERIENCE_ACHIEVEMENTS
-- ---------------------------------------------------------------------
CREATE TABLE experience_achievements (
    id              BIGSERIAL PRIMARY KEY,
    experience_id   BIGINT NOT NULL,
    text            VARCHAR(500) NOT NULL,
    display_order   INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_experience_ach_experience FOREIGN KEY (experience_id) REFERENCES experience (id) ON DELETE CASCADE
);
CREATE INDEX idx_experience_ach_experience_id ON experience_achievements (experience_id);

-- ---------------------------------------------------------------------
-- 13. EXPERIENCE_SKILLS (technologies used per role)
-- ---------------------------------------------------------------------
CREATE TABLE experience_skills (
    experience_id   BIGINT NOT NULL,
    skill_id        BIGINT NOT NULL,
    PRIMARY KEY (experience_id, skill_id),
    CONSTRAINT fk_experience_skills_experience FOREIGN KEY (experience_id) REFERENCES experience (id) ON DELETE CASCADE,
    CONSTRAINT fk_experience_skills_skill FOREIGN KEY (skill_id) REFERENCES skills (id) ON DELETE CASCADE
);
CREATE INDEX idx_experience_skills_skill_id ON experience_skills (skill_id);

-- ---------------------------------------------------------------------
-- 14. EDUCATION
-- ---------------------------------------------------------------------
CREATE TABLE education (
    id              BIGSERIAL PRIMARY KEY,
    degree          VARCHAR(200) NOT NULL,
    institution     VARCHAR(200) NOT NULL,
    location        VARCHAR(200),
    start_date      DATE NOT NULL,
    end_date        DATE,
    cgpa            NUMERIC(4,2),
    description     TEXT,
    display_order   INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_education_dates CHECK (end_date IS NULL OR end_date >= start_date),
    CONSTRAINT chk_education_cgpa CHECK (cgpa IS NULL OR (cgpa >= 0 AND cgpa <= 10))
);

-- ---------------------------------------------------------------------
-- 15. EDUCATION_ACHIEVEMENTS
-- ---------------------------------------------------------------------
CREATE TABLE education_achievements (
    id              BIGSERIAL PRIMARY KEY,
    education_id    BIGINT NOT NULL,
    text            VARCHAR(500) NOT NULL,
    display_order   INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_education_ach_education FOREIGN KEY (education_id) REFERENCES education (id) ON DELETE CASCADE
);
CREATE INDEX idx_education_ach_education_id ON education_achievements (education_id);

-- ---------------------------------------------------------------------
-- 16. CERTIFICATIONS
-- ---------------------------------------------------------------------
CREATE TABLE certifications (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    issuer          VARCHAR(200) NOT NULL,
    issue_date      DATE NOT NULL,
    credential_url  VARCHAR(500),
    image_url       VARCHAR(500),
    display_order   INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------
-- 17. BLOGS
-- ---------------------------------------------------------------------
CREATE TABLE blogs (
    id                      BIGSERIAL PRIMARY KEY,
    title                   VARCHAR(255) NOT NULL,
    slug                    VARCHAR(280) NOT NULL,
    content_markdown        TEXT NOT NULL,
    excerpt                 VARCHAR(500),
    cover_image_url         VARCHAR(500),
    category_id             BIGINT,
    author_id               BIGINT NOT NULL,
    status                  VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    views                   BIGINT NOT NULL DEFAULT 0,
    likes                   BIGINT NOT NULL DEFAULT 0,
    reading_time_minutes    INTEGER NOT NULL DEFAULT 0,
    published_at            TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_blogs_slug UNIQUE (slug),
    CONSTRAINT fk_blogs_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL,
    CONSTRAINT fk_blogs_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT chk_blogs_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED')),
    CONSTRAINT chk_blogs_views CHECK (views >= 0),
    CONSTRAINT chk_blogs_likes CHECK (likes >= 0)
);
CREATE INDEX idx_blogs_category_id ON blogs (category_id);
CREATE INDEX idx_blogs_author_id ON blogs (author_id);
CREATE INDEX idx_blogs_status ON blogs (status);
CREATE INDEX idx_blogs_published_at ON blogs (published_at);

-- ---------------------------------------------------------------------
-- 18. BLOG_TAGS
-- ---------------------------------------------------------------------
CREATE TABLE blog_tags (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    slug    VARCHAR(120) NOT NULL,
    CONSTRAINT uq_blog_tags_slug UNIQUE (slug)
);

-- ---------------------------------------------------------------------
-- 19. BLOG_TAG_MAP
-- ---------------------------------------------------------------------
CREATE TABLE blog_tag_map (
    blog_id BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,
    PRIMARY KEY (blog_id, tag_id),
    CONSTRAINT fk_blog_tag_map_blog FOREIGN KEY (blog_id) REFERENCES blogs (id) ON DELETE CASCADE,
    CONSTRAINT fk_blog_tag_map_tag FOREIGN KEY (tag_id) REFERENCES blog_tags (id) ON DELETE CASCADE
);
CREATE INDEX idx_blog_tag_map_tag_id ON blog_tag_map (tag_id);

-- ---------------------------------------------------------------------
-- 20. BLOG_COMMENTS
-- ---------------------------------------------------------------------
CREATE TABLE blog_comments (
    id              BIGSERIAL PRIMARY KEY,
    blog_id         BIGINT NOT NULL,
    name            VARCHAR(150) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    comment         TEXT NOT NULL,
    is_approved     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_blog_comments_blog FOREIGN KEY (blog_id) REFERENCES blogs (id) ON DELETE CASCADE
);
CREATE INDEX idx_blog_comments_blog_id ON blog_comments (blog_id);
CREATE INDEX idx_blog_comments_is_approved ON blog_comments (is_approved);

-- ---------------------------------------------------------------------
-- 21. MESSAGES (contact form submissions)
-- ---------------------------------------------------------------------
CREATE TABLE messages (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    subject     VARCHAR(255) NOT NULL,
    message     TEXT NOT NULL,
    is_read     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_messages_is_read ON messages (is_read);
CREATE INDEX idx_messages_created_at ON messages (created_at);

-- ---------------------------------------------------------------------
-- 22. RESUME
-- ---------------------------------------------------------------------
CREATE TABLE resume (
    id          BIGSERIAL PRIMARY KEY,
    file_url    VARCHAR(500) NOT NULL,
    file_name   VARCHAR(255) NOT NULL,
    version     INTEGER NOT NULL DEFAULT 1,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX uq_resume_single_active ON resume (is_active) WHERE is_active = TRUE;

-- ---------------------------------------------------------------------
-- 23. SOCIAL_LINKS
-- ---------------------------------------------------------------------
CREATE TABLE social_links (
    id              BIGSERIAL PRIMARY KEY,
    platform        VARCHAR(50) NOT NULL,
    url             VARCHAR(500) NOT NULL,
    icon            VARCHAR(100),
    display_order   INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT uq_social_links_platform UNIQUE (platform)
);

-- ---------------------------------------------------------------------
-- 24. SETTINGS (generic key-value site settings)
-- ---------------------------------------------------------------------
CREATE TABLE settings (
    id              BIGSERIAL PRIMARY KEY,
    setting_key     VARCHAR(150) NOT NULL,
    setting_value   TEXT,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_settings_key UNIQUE (setting_key)
);

-- ---------------------------------------------------------------------
-- 25. SEO (per-page SEO metadata)
-- ---------------------------------------------------------------------
CREATE TABLE seo (
    id                  BIGSERIAL PRIMARY KEY,
    page_path           VARCHAR(255) NOT NULL,
    meta_title          VARCHAR(255) NOT NULL,
    meta_description    VARCHAR(500),
    og_image_url        VARCHAR(500),
    canonical_url       VARCHAR(500),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_seo_page_path UNIQUE (page_path)
);

-- ---------------------------------------------------------------------
-- 26. VISITOR_LOGS
-- ---------------------------------------------------------------------
CREATE TABLE visitor_logs (
    id              BIGSERIAL PRIMARY KEY,
    ip_address      VARCHAR(64),
    user_agent      VARCHAR(500),
    page_path       VARCHAR(255) NOT NULL,
    referrer        VARCHAR(500),
    country         VARCHAR(100),
    city            VARCHAR(100),
    visited_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_visitor_logs_visited_at ON visitor_logs (visited_at);
CREATE INDEX idx_visitor_logs_page_path ON visitor_logs (page_path);

-- ---------------------------------------------------------------------
-- 27. ANALYTICS_DAILY (aggregated rollup, populated by scheduler)
-- ---------------------------------------------------------------------
CREATE TABLE analytics_daily (
    id                  BIGSERIAL PRIMARY KEY,
    metric_date         DATE NOT NULL,
    total_visits        BIGINT NOT NULL DEFAULT 0,
    unique_visitors     BIGINT NOT NULL DEFAULT 0,
    page_views          JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_analytics_daily_metric_date UNIQUE (metric_date)
);

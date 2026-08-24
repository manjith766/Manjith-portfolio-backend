-- =====================================================================
-- V3__add_resume_cloudinary_public_id.sql
--
-- The original schema stores file_url but not the Cloudinary public_id,
-- which is required to issue a deletion request against Cloudinary's API
-- when a resume version is superseded or removed. Without this column,
-- old resume files would accumulate in Cloudinary storage forever with
-- no way to clean them up programmatically.
--
-- Nullable because resume files uploaded through any other storage
-- provider (or manually inserted rows) may not have one — the app treats
-- a null public_id as "not deletable via Cloudinary API" rather than
-- failing.
-- =====================================================================

ALTER TABLE public.resume
    ADD COLUMN cloudinary_public_id VARCHAR(255);

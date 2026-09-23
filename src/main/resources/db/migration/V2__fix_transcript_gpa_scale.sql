-- The application uses a 5-point GPA scale (A=5, B=4, C=3, D=2, E=1).
-- Replace the original 4-point constraint so transcript recalculation cannot fail for A grades.
ALTER TABLE transcript DROP CONSTRAINT chk_transcript_gpa;
ALTER TABLE transcript ADD CONSTRAINT chk_transcript_gpa CHECK (gpa BETWEEN 0 AND 5.00);

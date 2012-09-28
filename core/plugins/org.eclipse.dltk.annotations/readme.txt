This bundle contains only @Nullable & @NonNull annotations with @Retention(RetentionPolicy.CLASS)
so it's not needed at runtime and should be referenced with the following line in build.properties:

additional.bundles = org.eclipse.dltk.annotations

The rationale for having own annotations:
- jsr-305 is not available in Orbit yet
- org.eclipse.jdt.annotation (3.8+) are not applicable to fields.

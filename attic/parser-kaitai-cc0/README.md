# parser-c00

This is a repackaging of CC0-1.0-licensed parsers from https://github.com/kaitai-io/kaitai_struct_formats
for maven central.

It is highly experimental.

All parser definitions without explicit CC0-1.0 license statements have been omitted.

All Java source code files with compilation errors have been excluded from compilation.

Please contact the project maintainers at https://github.com/kaitai-io if you have
problems that aren't related to the repackaging process.

Use `mvn -Pupdate clean install` to pull in updates from the kaitai project definitions master tree.

## Known Issues

### zip.ksy

Bit #3 of the general purpose flag is not supported (appended data descriptors).


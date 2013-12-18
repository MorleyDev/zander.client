include(${ZANDER_SOURCE_DIR}/spec/files.cmake)
include(${ZANDER_SOURCE_DIR}/spec/dependencies.cmake)

add_executable(zander_spec ${ZANDER_SPEC_FILES})
target_link_libraries(zander_spec ${ZANDER_SPEC_DEPENDENCIES})

add_test(spec zander_spec)

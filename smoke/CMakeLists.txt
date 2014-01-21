include(${ZANDER_SOURCE_DIR}/smoke/files.cmake)
include(${ZANDER_SOURCE_DIR}/smoke/dependencies.cmake)

add_executable(zander_smoke ${ZANDER_SMOKE_FILES})
target_link_libraries(zander_smoke ${ZANDER_SMOKE_DEPENDENCIES})

add_test(smoke zander_smoke)

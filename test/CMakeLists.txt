include(${ZANDER_SOURCE_DIR}/src/files.cmake)
include(${ZANDER_SOURCE_DIR}/src/dependencies.cmake)

include(${ZANDER_SOURCE_DIR}/test/files.cmake)
include(${ZANDER_SOURCE_DIR}/test/dependencies.cmake)

add_executable(zander_test ${ZANDER_TEST_FILES} ${ZANDER_SRC_FILES})
target_link_libraries(zander_test ${ZANDER_DEPENDENCIES} ${ZANDER_TEST_DEPENDENCIES})

add_test(unit zander_test)

include(${ZANDER_SOURCE_DIR}/src/files.cmake)
include(${ZANDER_SOURCE_DIR}/src/dependencies.cmake)

add_executable(zander ${ZANDER_MAIN_FILE} ${ZANDER_SRC_FILES})
target_link_libraries(zander ${ZANDER_SRC_DEPENDENCIES})

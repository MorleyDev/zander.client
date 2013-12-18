option(LINK_EXT_DIR "Add external to the search directories for libraries" ON)
set(ZANDER_EXT_DIRECTORY "${ZANDER_BINARY_DIR}/ext" CACHE FILEPATH "Path to external includes")

if (LINK_EXT_DIR)
	include(zander.cmake)

	include_directories(${ZANDER_EXT_DIRECTORY}/include)
	link_directories(${ZANDER_EXT_DIRECTORY}/lib)
endif()

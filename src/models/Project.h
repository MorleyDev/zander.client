#ifndef ZANDERCLIENT_MODELS_PROJECT_H_INCLUDED
#define ZANDERCLIENT_MODELS_PROJECT_H_INCLUDED

#include <string>

namespace zander
{
	namespace models
	{
		struct Project
		{
			std::string name;
			std::string os;
			std::string compiler;
		};
	}
}

#endif//ZANDERCLIENT_MODELS_PROJECT_H_INCLUDED

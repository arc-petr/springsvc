OS := $(shell uname)

preview: 
ifeq ($(OS),Darwin)
	sed -i "" -e "s/version:.*/version: $(PREVIEW_VERSION)/" Chart.yaml
	sed -i "" -e "s/version:.*/version: $(PREVIEW_VERSION)/" ../*/Chart.yaml
	sed -i "" -e "s/tag:.*/tag: $(PREVIEW_VERSION)/" values.yaml
else ifeq ($(OS),Linux)
	sed -i -e "s/version:.*/version: $(PREVIEW_VERSION)/" Chart.yaml
	sed -i -e "s/version:.*/version: $(PREVIEW_VERSION)/" ../*/Chart.yaml
<<<<<<< HEAD
	sed -i -e "s|repository:.*|repository: $(DOCKER_REGISTRY)\/arc-petr\/springsvc|" values.yaml
=======
	sed -i -e "s|repository:.*|repository: $(DOCKER_REGISTRY)\/fresh-catwalk-240714\/springsvc|" values.yaml
>>>>>>> b8346fa4231f482c78b6abf2b1ad2eda59e08ad9
	sed -i -e "s/tag:.*/tag: $(PREVIEW_VERSION)/" values.yaml
else
	echo "platfrom $(OS) not supported to release from"
	exit -1
endif
	echo "  version: $(PREVIEW_VERSION)" >> requirements.yaml
	jx step helm build

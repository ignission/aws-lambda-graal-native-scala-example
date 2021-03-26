.DEFAULT_GOAL := help

.PHONY: test
test: ## Format and build
	sbt 'fixAll; test'

check: ## Check sources
	sbt 'checkAll; compile; test'

.PHONY: dist
dist: ## Build and generate an executable binary
	sbt 'clean; fixAll; test; nativeImage'

.PHONY: help
help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

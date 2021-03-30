.DEFAULT_GOAL := help

.PHONY: test
test: ## Format and build
	sbt 'fixAll; test'

check: ## Check sources
	sbt 'checkAll; compile; test'

.PHONY: dist
dist: ## Build and generate an executable binary
	sbt 'clean; fixAll; test; nativeImage'

deploy: ## Deploy to cloud
	cd serverless/dist && chmod 755 bootstrap && zip lambda.zip bootstrap
	cd serverless && yarn && ./node_modules/serverless/bin/serverless.js deploy

.PHONY: help
help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

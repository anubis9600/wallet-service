package anubislab.tech.walletservice.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import graphql.schema.DataFetchingEnvironment;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {
    
    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        
        return new GraphQLError() {

            @Override
            public ErrorClassification getErrorType() {
                return ErrorType.DataFetchingException;
            }

            @Override
            public List<SourceLocation> getLocations() {
                /*List<SourceLocation> locations = new ArrayList<>();
                locations.add(env.getExecutionStepInfo().getField().getSourceLocation());*/
                return null;
            }

            @Override
            public String getMessage() {
                return ex.getMessage();
            }
            
        };
    }
}
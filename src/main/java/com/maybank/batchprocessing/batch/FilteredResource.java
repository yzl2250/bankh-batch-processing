package com.maybank.batchprocessing.batch;

import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class FilteredResource extends AbstractResource {

    private final Resource delegate;

    public FilteredResource(Resource delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getDescription() {
        return "FilteredResource wrapping -> " + delegate.getDescription();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(delegate.getInputStream(), StandardCharsets.UTF_8))) {
            String filtered = br.lines()
                    .filter(line -> line != null && !line.trim().isEmpty())
                    .collect(Collectors.joining(System.lineSeparator()));
            return new ByteArrayInputStream(filtered.getBytes(StandardCharsets.UTF_8));
        }
    }
}

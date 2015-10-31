package com.jacmobile.halloween.model;

import java.io.File;
import java.net.URI;

public class CameraFile extends File
{
    /**
     * Constructs a new File using the path of the specified URI. {@code uri} needs to be an absolute
     * and hierarchical Unified Resource Identifier with file scheme and non-empty path component, but
     * with undefined authority, query or fragment components.
     *
     * @param uri the Unified Resource Identifier that is used to construct this file.
     * @throws NullPointerException     if {@code uri == null}.
     * @throws IllegalArgumentException if {@code uri} does not comply with the conditions above.
     * @see #toURI
     * @see URI
     */
    public CameraFile(URI uri)
    {
        super(uri);
    }
}

package com.tntrip.understand;

import java.io.Serializable;

/**
 * Created by nuc on 2015/11/10.
 */
public interface SerializedLambda extends Serializable {
    // capture context
    String getCapturingClass();

    // SAM descriptor
    String getDescriptorClass();

    String getDescriptorName();

    String getDescriptorMethodType();

    // implementation
    int getImplReferenceKind();

    String getImplClass();

    String getImplName();

    String getImplMethodType();

    // dynamic args -- these will individually need to be Serializable too
    Object[] getCaptureArgs();


}

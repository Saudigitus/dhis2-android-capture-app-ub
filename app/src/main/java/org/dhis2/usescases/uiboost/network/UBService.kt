package org.dhis2.usescases.uiboost.network

import org.dhis2.usescases.uiboost.data.model.MessageResponse
import retrofit2.http.GET
import retrofit2.http.Path
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

interface UBService {

  @GET("documents/{uid}/data")
  suspend fun downloadFileResource(
    @Path("uid") uid: String
  ): Byte
}
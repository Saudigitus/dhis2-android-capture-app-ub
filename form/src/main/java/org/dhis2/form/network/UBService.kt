package org.dhis2.usescases.uiboost.network

import okhttp3.ResponseBody
import org.dhis2.form.data.media.MediaDetails
import org.dhis2.form.network.MessageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UBService {

  @GET("documents/{uid}/data")
  suspend fun downloadFileResource(
    @Path("uid") uid: String
  ): Response<ResponseBody>

  @GET("documents/{uid}")
  suspend fun getMediaDetails(
    @Path("uid") uid: String
  ): Response<MediaDetails>
}
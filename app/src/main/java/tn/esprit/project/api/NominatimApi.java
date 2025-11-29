package tn.esprit.project.api;



import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import tn.esprit.project.models.NominatimResponse;

public interface NominatimApi {

    @GET("search")
    Call<List<NominatimResponse>> searchAddress(
            @Query("q") String query,
            @Query("format") String format,
            @Query("addressdetails") int addressdetails,
            @Query("limit") int limit,
            @Query("countrycodes") String countrycodes   // <--- 5ème paramètre
    );
}

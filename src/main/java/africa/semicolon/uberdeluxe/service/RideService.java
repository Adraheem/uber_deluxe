package africa.semicolon.uberdeluxe.service;

import africa.semicolon.uberdeluxe.data.dto.request.BookRideRequest;
import africa.semicolon.uberdeluxe.data.dto.response.ApiResponse;
import africa.semicolon.uberdeluxe.data.dto.response.PageDto;
import africa.semicolon.uberdeluxe.data.dto.response.RideDto;
import org.springframework.data.domain.Pageable;

public interface RideService {
    ApiResponse bookRide(BookRideRequest request);

    PageDto<RideDto> getRideHistory(Pageable pageable);

}

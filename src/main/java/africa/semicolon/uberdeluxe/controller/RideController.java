package africa.semicolon.uberdeluxe.controller;

import africa.semicolon.uberdeluxe.data.dto.request.BookRideRequest;
import africa.semicolon.uberdeluxe.data.dto.response.ApiResponse;
import africa.semicolon.uberdeluxe.data.dto.response.PageDto;
import africa.semicolon.uberdeluxe.data.dto.response.RideDto;
import africa.semicolon.uberdeluxe.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ride")
@AllArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping("/book")
    @Operation(summary = "Book a ride")
    public ResponseEntity<ApiResponse> bookRide(@RequestBody BookRideRequest request){
        return ResponseEntity.ok(rideService.bookRide(request));
    }

    @GetMapping("")
    @Operation(summary = "Get ride history")
    public ResponseEntity<PageDto<RideDto>> getHistory(@ParameterObject Pageable pageable){
        return ResponseEntity.ok(rideService.getRideHistory(pageable));
    }
}

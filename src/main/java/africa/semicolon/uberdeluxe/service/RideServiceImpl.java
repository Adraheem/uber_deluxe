package africa.semicolon.uberdeluxe.service;

import africa.semicolon.uberdeluxe.config.distance.DistanceConfig;
import africa.semicolon.uberdeluxe.data.dto.request.BookRideRequest;
import africa.semicolon.uberdeluxe.data.dto.request.LocationDto;
import africa.semicolon.uberdeluxe.data.dto.response.*;
import africa.semicolon.uberdeluxe.data.models.Location;
import africa.semicolon.uberdeluxe.data.models.Passenger;
import africa.semicolon.uberdeluxe.data.models.Ride;
import africa.semicolon.uberdeluxe.data.repositories.RideRepository;
import africa.semicolon.uberdeluxe.exception.BusinessLogicException;
import africa.semicolon.uberdeluxe.util.AppUtilities;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Service
@AllArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final DistanceConfig directionConfig;
    private final PassengerService passengerService;
    private final ModelMapper modelMapper;


    @Override
    public ApiResponse bookRide(BookRideRequest request) {
        //1. find passenger
        Passenger foundPassenger = passengerService.getCurrentPassenger();
        //2. calculate distance between origin and destination
        DistanceMatrixElement distanceInformation = getDistanceInformation(request.getOrigin(), request.getDestination());
        //3. calculate eta
        String eta = distanceInformation.getDuration().getText();
        //4. calculate price
        BigDecimal fare = AppUtilities.calculateRideFare(distanceInformation.getDistance().getText());
        Ride ride = Ride.builder()
                .passenger(foundPassenger)
                .origin(modelMapper.map(request.getOrigin(), Location.class))
                .destination(modelMapper.map(request.getDestination(), Location.class))
                .eta(eta)
                .fare(fare)
                .build();
        rideRepository.save(ride);
        return ApiResponse.builder().fare(fare).estimatedTimeOfArrival(eta).build();
    }

    @Override
    public PageDto<RideDto> getRideHistory(Pageable pageable) {
        Passenger passenger = passengerService.getCurrentPassenger();
        Page<Ride> rides = rideRepository.findAllByPassenger(passenger, pageable);

        Type pageDtoTypeToken = new TypeToken<PageDto<RideDto>>() {
        }.getType();
        return modelMapper.map(rides, pageDtoTypeToken);
    }

    private DistanceMatrixElement getDistanceInformation(LocationDto origin, LocationDto destination) {
        RestTemplate restTemplate = new RestTemplate();
        String url = buildDistanceRequestUrl(origin, destination);
        ResponseEntity<GoogleDistanceResponse> response =
                restTemplate.getForEntity(url, GoogleDistanceResponse.class);
        return Objects.requireNonNull(response.getBody()).getRows().stream()
                .findFirst().orElseThrow(()-> new BusinessLogicException("Distance error"))
                .getElements().stream()
                .findFirst()
                .orElseThrow(()-> new BusinessLogicException("Distance error"));
    }

    private  String buildDistanceRequestUrl(LocationDto origin, LocationDto destination){;
        return directionConfig.getGoogleDistanceUrl()+"/"+ AppUtilities.JSON_CONSTANT+"?"
                +"destinations="+AppUtilities.buildLocation(destination)+"&origins="
                +AppUtilities.buildLocation(origin)+"&mode=driving"+"&traffic_model=pessimistic"
                +"&departure_time="+ LocalDateTime.now().toEpochSecond(ZoneOffset.of("+01:00"))
                +"&key="+directionConfig.getGoogleApiKey();
    }
}

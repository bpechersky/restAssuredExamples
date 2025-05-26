package data.restfulbooker;

import lombok.Builder;
import lombok.Getter;
import lombok.Data;

/**
 * Created By Faisal Khatri on 18-02-2022
 */
@Data
@Getter
@Builder
public class PartialBookingData {
    String firstname;
    int    totalprice;
}
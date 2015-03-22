package devbury.keepdeweyalive;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PingController {

    @RequestMapping("/ping")
    public Response ping(HttpServletRequest httpServletRequest) {
        Response response = new Response();
        response.setMessage("pinging now");
        response.setPingUrl("http://" + httpServletRequest.getRemoteAddr() + "/health");
        return response;
    }
}

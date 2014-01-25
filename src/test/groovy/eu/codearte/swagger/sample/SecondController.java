package eu.codearte.swagger.sample;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Jakub Kubrynski / 2014-01-25
 */
@Controller
@RequestMapping("/second")
public class SecondController {

	@RequestMapping(value = "/void", method = RequestMethod.GET)
	public void voidMethod() {
		return;
	}
}

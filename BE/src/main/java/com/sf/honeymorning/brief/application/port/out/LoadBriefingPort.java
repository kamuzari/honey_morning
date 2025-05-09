package com.sf.honeymorning.brief.application.port.out;

import com.sf.honeymorning.brief.application.domain.TtsBriefing;

public interface LoadBriefingPort {
	TtsBriefing getTtsBriefingWithQuizzes(Long id);

}

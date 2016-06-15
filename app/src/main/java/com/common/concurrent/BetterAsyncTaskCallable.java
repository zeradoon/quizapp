/* Copyright (c) 2009 Matthias Käppler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.common.concurrent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author jungungi
 *
 */
public interface BetterAsyncTaskCallable {

	
    /**
     * Method ID  : getAsyncTaskResult
     * Method 설명 : AsynTask에서 실행된 결과를 callback 받는다. 
     * 최초작성일  : 2011. 9. 22. 
     * 작성자 : jungungi
     * 변경이력 : 
     * @param list AsynTask의 실행 결과를 ArrayList<HashMap<String, String>> 타입으로 넘겨준다.
     * @param id
     * @return 
     */
    public boolean getAsyncTaskResult(ArrayList<HashMap<String, String>> list, int id);

}

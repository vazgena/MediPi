/*
 Copyright 2016  Richard Robinson @ NHS Digital <rrobinson@nhs.net>

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.medipi.clinical.dao;

import java.util.List;
import org.medipi.clinical.entities.SimpleMessage;
import org.springframework.stereotype.Repository;

/**
 * Implementation of Data Access Object for SimpleMessage
 * @author rick@robinsonhq.com
 */
@Repository
public class SimpleMessageDAOImpl extends GenericDAOImpl<SimpleMessage> implements SimpleMessageDAO {
    @Override
    public List<SimpleMessage> findByNullTransmitSuccessDate(int maxRetries) {
        return this.getEntityManager().createNamedQuery("SimpleMessage.findByNullTransmitSuccessDate", SimpleMessage.class)
                .setParameter("maxRetries", maxRetries)
                .getResultList();
    }
}
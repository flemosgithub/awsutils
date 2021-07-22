package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should send email with aws"
    request{
        method POST()
        url "/mail/send"
        headers {
            header 'Content-Type': 'application/json'
        }
        body '''
            {
              "fromAddress": "fabio.lemos@multilaser.com.br",
              "toAddress": "mogi@multilaser.com.br",
              "subject": "mock data for contract",
              "textBody": "a simple mock data in order to implement springcloud contract",
              "htmlBody": "a simple mock data in order to implement springcloud contract"
            }
        '''
    }
    response {
        status 200
    }
}
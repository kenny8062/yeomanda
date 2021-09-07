const should = require('should')
const request = require('supertest')
const assert = require('assert')
const sinon = require('sinon')
const faker = require("faker");
const chai = require('chai')
const expect = chai.expect
const jest = require('jest-mock')

/**
 * function의 파라미터로 done을 넣어주면 해당 함수를 비동기 함수로 취급하고 비동기 로직이 끝나면 callback으로 done이 실행되고 테스트 코드는 끝이난다. 
 * 만약에 done을 실행하지 않으면 2000ms 이 지났을 경우 타임아웃 실패로 처리한다.
 * 
 * 과연 데이터베이스를 접근하지 않는 가장 간단한 테스트 코드는 어떻게 작성하는 것인가...
 */


describe("Test suite", () => {
    it("should be ok", () => {
      assert.equal(true, true)
    })
})

const mockRequest = () => {
    const req = {}
    req.body = jest.fn().mockReturnValue(req)
    req.params = jest.fn().mockReturnValue(req)
    return req
}

const mockResponse = () => {
    const res = {}
    res.send = jest.fn().mockReturnValue(res)
    res.status = jest.fn().mockReturnValue(res)
    res.json = jest.fn().mockReturnValue(res)
    return res
}

// describe("Check method userController.login ", () => {
//     it('should 200 and return yes', async() => {
//         const req = mockRequest();
//         const res = mockResponse();
  
//         await userController.testing(req, res);
//         expect(res.send.mock.calls[0][0]).equal('yes')
//     });
// });

// describe("Check method userController.login ", () => {
//     it('should 200 and return yes', async() => {
//         //const req = mockRequest();
//         //const req = { body: { email: faker.internet.email(), password: faker.internet.password() } };
//         const req = { body: { email: 'woals1@woals1.com', password: 'woals1'}}
//         const res = mockResponse();
  
//         await userController.login(req, res);
//         expect(res.send.mock.calls[0][0].message).equal('로그인 성공')
//     });
// });

/**
 * complex test code that run in local below
 */

// describe('POST /users', () => {
//     it('return users', (done) => {
//         request(app_test)
//         .post('/user/login')
//         .send({email: 'woals1@woals1.com', password: 'woals1'})
//         .expect(200)
//         .end((err, res) => {
//             if(err){
//                 throw err;
//             }
//             res.body.should.have.property('success');
//         });
//         done(); 
//     });
// });
// describe('POST /user', () => {
//     // signup shoule get files...

//     // it('signup should return success', (done) => {
//     //     request(app)
//     //     .post('/user/signup')
//     //     .send({email: 'testcode@testcode.com', password: 'testcode', name: 'testcode', birth: 'testcode', sex: 'M'})
//     //     .expect(200)
//     //     .end((err, res) => {
//     //         if(err){
//     //             throw err;
//     //         }
//     //         res.body.should.have.property('success');
//     //     });
//     //     done();
//     // });

//     it('login should return success',(done) => {
//         request(app_test)
//         .post('/user/login')
//         .send({email: 'woals1@woals1.com', password: 'woals1'}) // 실제로 서버를 통해 dynamodb에 접속하기 때문에 실제 존재하는 계정으로 테스트한다. 
//         .expect(200)
//         .end((err, res) => {
//             if(err){
//                 throw err;
//             }
//             res.body.should.have.property('success');
//         });
//         done(); 
//     });
// })

// describe('POST /travelers', () => {
//     it('showTravelers return list of user data & success', (done) => {
//         request(app_test)
//         .post('/travelers/showTravelers')
//         .send({latitude: '40.6643', longitude: '-73.9385'})
//         .expect(200)
//         .end((err, res) => {
//             if(err){
//                 throw err
//             }
//             res.body.data.should.have.property('teams');
//             res.body.data.should.have.property('nameList');
//         });
//         done();
//     });

//     // it('registerplan return success', (done) => {
//     //     request(app)
//     //     .post('/travelers/registerPlan')
//     //     .send([
//     //         /**
//     //          * travelMate -> email(pk) : 중복되게 테스트하면 안됨. 항상 다른 값으로 해야함 -> 그래서 주석처리함.
//     //          */
//     //         {latitude : '40.6643', longitude: '-73.9385', travelMate : "testcode4", travelDate : "20201234"},
//     //         {latitude : '40.6643', longitude: '-73.9385', travelMate : "testcode5", travelDate : "20201234"}
//     //     ])
//     //     .expect(200)
//     //     .end((err, res) => {
//     //         if(err){
//     //             throw err
//     //         }
//     //         res.body.should.have.property('success');
//     //     })
//     //     done();
//     // })
// })


// describe('POST /markup', () => {
//     it('it should return success', (done) => {
//         request(app)
//         .post('/markup/favorite')
//         .send({email : 'woals1@woals1.com', favorite_team_no : 909})
//         .expect(200)
//         .end((err, res) => {
//             if(err){
//                 throw err
//             }
//             res.body.should.have.property('success');
//         });
//         done();
//     })
// })

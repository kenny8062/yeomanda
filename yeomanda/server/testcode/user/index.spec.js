const { json } = require('express')
const should = require('should')
const request = require('supertest')
const app = require('../../app')

describe('POST /user', () => {
    it('signup should return success', (done) => {
        request(app)
        .post('/user/signup')
        .send({email: 'test4@test4.com', password: 'test4', name: 'test4', birth: '1996/07/27'})
        .expect(200)
        .end((err, res) => {
            if(err){
                throw err;
            }
            res.body.should.have.property('success');
        });
        done();
    });

    it('login should return success',(done) => {
        request(app)
        .post('/user/login')
        .send({email: 'test2@test2.com', password: 'test2'}) // 실제로 서버를 통해 dynamodb에 접속하기 때문에 실제 존재하는 계정으로 테스트한다. 
        .expect(200)
        .end((err, res) => {
            if(err){
                throw err;
            }
            res.body.should.have.property('success');
        });
        done();
    });
})



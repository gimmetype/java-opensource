import React, { useState, useEffect, useRef } from 'react';
import {Button, CloseButton, ListGroup, ListGroupItem} from 'react-bootstrap';

function Group({setChosen, list, idx, select}) {
    let mylist = [];
    if (list.filter !== undefined) mylist = list.filter(e => e['groupidx'] === idx);
    return (
        <div className='rounded-3' style={{
            background: '#868e96',
            margin: '5px'}}>
            <ListGroup as="ol" numbered>
                <header style={{display:'flex'}}>
                    <h4 style={{margin: '10px'}}>그룹 {idx}</h4>
                    <Button size="sm" variant="primary" style={{marginTop:'2%', height: '75%'}} onClick={() => {
                        if ('id' in select) {
                            let already = false;
                            for (let i = 0; i < list.length; i++) {
                                if (list[i].id === select.id) {
                                    already = true;
                                    break;
                                }
                            }
                            if (!already) {
                                select.groupidx = idx;
                                if (list.length === 0)
                                    setChosen([select]);
                                else
                                    setChosen([...list, select]);
                            }
                        }}}>추가</Button>
                </header>
                {mylist.map(item => (
                    <ListGroup.Item key={item['id']} as="li">
                        {item['과목명']} {item['담당교수']}
                        <CloseButton style={{alignSelf: 'end'}} onClick={() => {
                            setChosen(list.filter((e) => (e['id'] !== item['id'])));
                        }}/>
                    </ListGroup.Item>
                ))}
            </ListGroup>
        </div>
    );
}

export default Group;
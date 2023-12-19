// 필요한 모듈 가져오기
import React, { useState, useEffect, useRef } from 'react';
import './App.css';
import TableWithScrollBar from './TableWithScrollBar.js';

// 주요 컴포넌트 정의
const App = () => {
    // 상태 변수들
    const [csvData, setCsvData] = useState([]);
    const [selectedRows, setSelectedRows] = useState([]);
    const [groups, setGroups] = useState([]);
    const [draggedRowId, setDraggedRowId] = useState(null);
    const [isDragging, setIsDragging] = useState(false);
    const tableRef = useRef(null);

    // CSV 데이터 및 초기 그룹 생성(fetch) useEffect
    useEffect(() => {
        // CSV 데이터 가져오기
        fetch('./lecture_raw_data.csv')
            .then((response) => response.text())
            .then((data) => {
                const rows = data.split('\n');
                const parsedData = rows.map((row, index) => {
                    const columns = row.split(',');
                    return {
                        id: index + 1,
                        grade: columns[1],
                        courseType: columns[2],
                        subjectCode: columns[3],
                        subjectName: columns[4],
                        division: columns[5],
                        credit: columns[6],
                        department: columns[7],
                        major: columns[8],
                        professorAffiliation: columns[9],
                        professor: columns[10],
                        classTime: columns[11],
                    };
                });
                setCsvData(parsedData);
            })
            .catch((error) => {
                console.error('CSV 파일을 읽는 중 오류 발생:', error);
            });

        // 초기에 5개의 그룹 생성
        const initialGroups = Array.from({ length: 5 }, (_, index) => ({
            id: index + 1,
            name: `그룹 ${index + 1}`,
            classes: [],
        }));
        setGroups(initialGroups);
    }, []); // 두 번째 인자가 빈 배열이므로 컴포넌트가 처음 마운트될 때만 실행됩니다.

    // 행 클릭 시 호출될 함수
    const handleRowClick = (rowId) => {
        const selectedRowData = csvData.find((row) => row.id === rowId);
        setSelectedRows((prevRows) => [...prevRows, selectedRowData]);
    };

    // 그룹 추가 시 호출될 함수
    const handleAddGroup = () => {
        const newGroup = { id: groups.length + 1, name: `Group ${groups.length + 1}`, classes: [] };
        setGroups((prevGroups) => [...prevGroups, newGroup]);
    };

// 수업을 그룹에 추가 시 호출될 함수
    const handleAddClassToGroup = (groupId, classId) => {
        setGroups((prevGroups) => {
            return prevGroups.map((group) => {
                if (group.id === groupId) {
                    const selectedClass = csvData.find((row) => row.id === classId);

                    // Check if the class is not already in the group
                    const isClassAlreadyInGroup = group.classes.some((classItem) => classItem.id === selectedClass.id);

                    if (!isClassAlreadyInGroup) {
                        group.classes.push(selectedClass);

                        // 드롭한 후에는 더 이상 추가하지 않도록 해당 수업을 CSV 데이터에서 제거
                        setCsvData((prevData) => prevData.filter((row) => row.id !== classId));
                    }
                }
                return group;
            });
        });
    };


    // 드래그 시작 시 호출될 함수
    const handleDragStart = (e, rowId) => {
        e.dataTransfer.setData('text/plain', rowId.toString());
        setDraggedRowId(rowId);
        setIsDragging(true);
    };

    // 드래그 중 호출될 함수
    const handleDragOver = (e) => {
        e.preventDefault();
    };

    // 드롭 시 호출될 함수
    const handleDrop = (e, groupId) => {
        e.preventDefault(); // 기본 동작 막기

        if (isDragging && draggedRowId !== null) {
            const selectedRowData = csvData.find((row) => row.id === draggedRowId);

            // 그룹에 수업 추가
            handleAddClassToGroup(groupId, draggedRowId);

            // 드래그 중인 행을 원래 위치에서 제거
            // setCsvData((prevData) => {
            //     const newData = prevData.filter((row) => row.id !== draggedRowId);
            //     return newData;
            // });
        }
        setDraggedRowId(null);
        setIsDragging(false);
    };

    // 그룹에서 수업 제거 시 호출될 함수
    const handleRemoveClassFromGroup = (groupId, classId) => {
        setGroups((prevGroups) => {
            const updatedGroups = prevGroups.map((group) => {
                if (group.id === groupId) {
                    // 그룹에서 해당 수업 제거
                    group.classes = group.classes.filter((classItem) => classItem.id !== classId);
                }
                return group;
            });
            return updatedGroups;
        });

        // 제거된 수업을 다시 CSV 데이터에 추가
        setCsvData((prevData) => {
            const removedClass = groups
                .flatMap((group) => group.classes)
                .find((classItem) => classItem.id === classId);

            if (removedClass) {
                return [...prevData, removedClass];
            }

            return prevData;
        });
    };

    return (
        <div>
            <h1>수업 시간표</h1>

            {/* 첫 번째 테이블 */}
            <TableWithScrollBar
                data={csvData}
                onRowClick={handleRowClick}
                onDragStart={handleDragStart}
                onDragOver={handleDragOver}
                onDrop={handleDrop}
                tableRef={tableRef}
            />
            <br/><br/>
            {/* 두 번째 테이블 */}
            <div style={{display: 'flex', alignItems: 'center'}}>
                <h2>시간표 그룹</h2>
                <button onClick={handleAddGroup}>그룹 추가하기</button>
            </div>
            {groups.map((group) => (
                <div key={group.id} onDrop={(e) => handleDrop(e, group.id)} onDragOver={handleDragOver}>
                    <h3>{group.name}</h3>
                    <table>
                        {/* 테이블 헤더 */}
                        <thead>
                        <tr>
                            <th>순번</th>
                            <th>학년</th>
                            <th>이수구분</th>
                            <th>과목코드</th>
                            <th>과목명</th>
                            <th>분반</th>
                            <th>학점</th>
                            <th>개설대학</th>
                            <th>개설학과</th>
                            <th>대표교수소속</th>
                            <th>담당교수</th>
                            <th>수업시간</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        {/* 테이블 내용 */}
                        <tbody>
                        {group.classes.map((classItem) => (
                            <tr key={classItem.id}>
                                <td>{classItem.id}</td>
                                <td>{classItem.grade}</td>
                                <td>{classItem.courseType}</td>
                                <td>{classItem.subjectCode}</td>
                                <td>{classItem.subjectName}</td>
                                <td>{classItem.division}</td>
                                <td>{classItem.credit}</td>
                                <td>{classItem.department}</td>
                                <td>{classItem.major}</td>
                                <td>{classItem.professorAffiliation}</td>
                                <td>{classItem.professor}</td>
                                <td>{classItem.classTime}</td>
                                <td>
                                    <button onClick={() => handleRemoveClassFromGroup(group.id, classItem.id)}>
                                        Remove
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            ))}
        </div>
    );
};

// 컴포넌트 내보내기
export default App;